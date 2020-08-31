/*
 * Copyright (c) 2020 Talent Beyond Boundaries. All rights reserved.
 */

package org.tbbtalent.server.service.db.impl;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.tbbtalent.server.exception.InvalidRequestException;
import org.tbbtalent.server.exception.SalesforceException;
import org.tbbtalent.server.model.db.Candidate;
import org.tbbtalent.server.model.db.User;
import org.tbbtalent.server.model.sf.Contact;
import org.tbbtalent.server.service.db.SalesforceService;
import org.tbbtalent.server.service.db.email.EmailHelper;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Mono;

/**
 * Standard implementation of Salesforce service
 *
 * @author John Cameron
 */
@Service
public class SalesforceServiceImpl implements SalesforceService, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(SalesforceServiceImpl.class);

    private boolean alertedDuplicateSFRecord = false;

    private final Map<Class<?>,String> classSfpathMap = new HashMap<>();

    private final String contactRetrievalFields = "Id,TBBId__c";
    
    private final EmailHelper emailHelper;

    @Value("${salesforce.privatekey}")
    private String privateKeyStr;
    
    private PrivateKey privateKey;

    @Value("${salesforce.tbb.jordanAccount}")
    private String tbbJordanAccountId;

    @Value("${salesforce.tbb.lebanonAccount}")
    private String tbbLebanonAccountId;

    private final WebClient webClient;
    
    /**
     * This is the accessToken required for for all Salesforce REST API
     * calls. It should appear in the Authroization header, prefixed by
     * "Bearer ".
     * <p/>
     * The token is retrieved from Salesforce by calling 
     * {@link #requestAccessToken()}
     * <p/>
     * Access tokens expire so they need to be rerequested periodically.
     */
    private String accessToken = null;

    @Autowired
    public SalesforceServiceImpl(EmailHelper emailHelper) {
        this.emailHelper = emailHelper;
        
        classSfpathMap.put(ContactRequest.class, "Contact");
        
        WebClient.Builder builder = 
                WebClient.builder()
                .baseUrl("https://talentbeyondboundaries.my.salesforce.com/services/data/v49.0")
                .defaultHeader("Content_Type","application/json")
                .defaultHeader("Accept","application/json");

        webClient = builder.build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        privateKey = privateKeyFromPkcs8(privateKeyStr);
    }

    @Override
    @NonNull
    public Contact createContact(@NonNull Candidate candidate)
            throws GeneralSecurityException, WebClientException,
            SalesforceException {

        //Create a contact request using data from the candidate
        ContactRequest contactRequest = new ContactRequest(candidate);

        //Execute a create request
        ClientResponse response = executeCreate(contactRequest);

        //And decode the response
        CreateRecordResult result = response.bodyToMono(CreateRecordResult.class).block();

        assert result != null;
        if (!result.success) {
            StringBuilder builder = new StringBuilder();
            for (String error : result.errors) {
                builder.append(error);
                builder.append('\n');
            }
            throw new SalesforceException(builder.toString());
        }


        Contact contact = new Contact(candidate);
        contact.setId(result.id);

        return contact;
    }

    @Override
    @Nullable
    public Contact findContact(@NonNull Candidate candidate) 
            throws GeneralSecurityException, WebClientException {

        String candidateNumber = candidate.getCandidateNumber();
        
        //Note that the fields requested in the query should match the fields
        //in the Contact record.
        String query = 
                "SELECT " + contactRetrievalFields + 
                        " FROM Contact WHERE TBBId__c=" + candidateNumber;
        
        ClientResponse response = executeQuery(query);
        ContactQueryResult contacts = response.bodyToMono(ContactQueryResult.class).block();

        //Retrieve the contact from the response 
        Contact contact = null;
        if (contacts != null) {
            if (contacts.totalSize > 0) {
                final int nContacts = contacts.records.size();
                if (nContacts > 0) {
                    contact = contacts.records.get(0);
                    if (nContacts > 1) {
                        //We have multiple contacts in Salesforce for the same 
                        //TBB candidate. There should only be one.
                        final String msg = "Candidate number " + candidateNumber + 
                                " has more than one Contact record on Salesforce";
                        log.warn(msg);
                        if (!alertedDuplicateSFRecord) {
                            emailHelper.sendAlert(msg);
                            alertedDuplicateSFRecord = true;
                        }
                    }
                }
            }
        }
        
        return contact;
    }

    @Override
    public List<Contact> findCandidateContacts() 
            throws GeneralSecurityException, WebClientException {
        return findContacts("TBBid__c > 0");
    }

    @Override
    public List<Contact> findContacts(String condition) 
            throws GeneralSecurityException, WebClientException {
        String query =
                "SELECT " + contactRetrievalFields + 
                        " FROM Contact WHERE " + condition;

        ClientResponse response = executeQuery(query);
        ContactQueryResult result = 
                response.bodyToMono(ContactQueryResult.class).block();

        //Retrieve the contact from the response 
        List<Contact> contacts = null;
        if (result != null) {
            contacts = result.records;
        }
        
        return contacts;
    }

    @Override
    public void updateContact(Candidate candidate) 
            throws GeneralSecurityException {
        //Create a contact request using data from the candidate
        ContactRequest contactRequest = new ContactRequest(candidate);

        String salesforceId = candidate.getSfId();
        if (salesforceId == null) {
            throw new SalesforceException(
                    "Could not find candidate " + 
                            candidate.getCandidateNumber() + 
                            " on Salesforce from sflink " + 
                            candidate.getSflink());             
        }
        
        //Execute the update request
        executeUpdate(salesforceId, contactRequest);
    }

    /**
     * Execute general purpose Salesforce create.
     * <p/>
     * For details on Salesforce create, see 
     * https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_sobject_create.htm     
     * @param obj Object supplying data used to create corresponding Salesforce
     *            record.
     * @return ClientResponse - can use bodyToMono method to extract into an object.
     * @throws GeneralSecurityException If there are errors relating to keys
     * and digital signing.
     * @throws WebClientException if there is a problem connecting to Salesforce
     */
    private ClientResponse executeCreate(Object obj)
            throws GeneralSecurityException, WebClientException {

        Class<?> cl = obj.getClass();
        String path = classSfpathMap.get(cl);
        if (path == null) {
            throw new InvalidRequestException(
                    "No mapping to Salesforce for objects of class " + cl.getSimpleName());
        }

        WebClient.RequestHeadersSpec<?> spec = webClient.post()
                .uri("/sobjects/" + path)
                .body(Mono.just(obj), cl);

        return executeWithRetry(spec);
    }

    /**
     * Execute general purpose Salesforce update.
     * <p/>
     * For details on Salesforce update, see
     * https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_update_fields.htm
     * <p/>
     * Note that there is no body in the response - so this method just returns void
     * @param id Salesforce id for the record
     * @param obj Object supplying data used to update the Salesforce record.
     * @throws GeneralSecurityException If there are errors relating to keys
     * and digital signing.
     * @throws WebClientException if there is a problem connecting to Salesforce
     */
    private void executeUpdate(String id, Object obj)
            throws GeneralSecurityException, WebClientException {

        Class<?> cl = obj.getClass();
        String path = classSfpathMap.get(cl);
        if (path == null) {
            throw new InvalidRequestException(
                    "No mapping to Salesforce for objects of class " + cl.getSimpleName());
        }

        WebClient.RequestHeadersSpec<?> spec = webClient.patch()
                .uri("/sobjects/" + path + "/" + id)
                .body(Mono.just(obj), cl);

        ClientResponse response = executeWithRetry(spec);
        
        //Only a 204 response is expected - and no body.
        if (response.rawStatusCode() != 204) {
            WebClientException ex = response.createException().block();
            assert ex != null;
            throw ex;
        }
    }

    /**
     * Execute general purpose Salesforce query.
     * <p/>
     * For details on Salesforce queries, see 
     * https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_query.htm
     * @param query Query to be executed 
     * @return ClientResponse - can use bodyToMono method to extract into an object.
     * @throws GeneralSecurityException If there are errors relating to keys
     * and digital signing.
     * @throws WebClientException if there is a problem connecting to Salesforce
     */
    private ClientResponse executeQuery(String query) 
            throws GeneralSecurityException, WebClientException {

        WebClient.RequestHeadersSpec<?> spec = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/query")
                        .queryParam("q", query).build());
        
        return executeWithRetry(spec);
    }

    /**
     * Executes a request with a retry if the accessToken validity has expired
     * in which case another accessToken is automatically requested.
     * @param spec for the request 
     * @return ClientResponse received
     * @throws GeneralSecurityException if there is a problem with our keys
     * and digital signing.
     * @throws WebClientException if there is a problem connecting to Salesforce
     */
    private ClientResponse executeWithRetry(WebClient.RequestHeadersSpec<?> spec) 
            throws GeneralSecurityException, SalesforceException {
        if (accessToken == null) {
            accessToken = requestAccessToken();
        }
        spec.headers(headers -> headers.put("Authorization",
                Collections.singletonList("Bearer " + accessToken)));
        ClientResponse clientResponse = spec.exchange().block();
        if (clientResponse == null ||  clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
            //Get new token and try again
            accessToken = requestAccessToken();
            spec.headers(headers -> headers.put("Authorization",
                    Collections.singletonList("Bearer " + accessToken)));
            clientResponse = spec.exchange().block();
        }

        if (clientResponse == null) {
            throw new RuntimeException("Null client response to Salesforce request");
        } else if (clientResponse.rawStatusCode() == 300 || 
                clientResponse.rawStatusCode() == 400) {
            //Pull out the extra info on the error provided by Salesforce.
            //See https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/errorcodes.htm
            String errorInfo = clientResponse.bodyToMono(String.class).block();
            //Create an exception and use their message. The exception by itself
            //is no use because it doesn't decode the above Salesforce error
            //info from the body of the response.
            WebClientException ex = clientResponse.createException().block();
            assert ex != null;
            
            //Create our own exception with the extra info.
            throw new SalesforceException(ex.getMessage() + ": " + errorInfo);
        } else if (clientResponse.rawStatusCode() > 300) {
            WebClientException ex = clientResponse.createException().block();
            assert ex != null;
            throw ex;
        }
        return clientResponse;
    }

    /**
     * Requests an accessToken from Salesforce.
     * <p/>
     * This involves making a special, digitally signed, JWT Bearer Token 
     * request to SF.
     * <p/>
     * See
     * https://help.salesforce.com/articleView?id=remoteaccess_oauth_jwt_flow.htm&type=5
     * @return Access token provided by Salesforce
     * @throws GeneralSecurityException if there is a problem with our keys
     * and digital signing.
     */
    private @NonNull String requestAccessToken() throws GeneralSecurityException {
        String jwtBearerToken = makeJwtBearerToken();
        
        WebClient client = WebClient
                .create("https://login.salesforce.com/services/oauth2/token");
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type",
                "urn:ietf:params:oauth:grant-type:jwt-bearer");
        params.add("assertion", jwtBearerToken);
        
        BearerTokenResponse response = client.post()
                .uri(uriBuilder -> uriBuilder.queryParams(params).build())
                .header("Content_Type", 
                        "application/x-www-form-urlencoded")
                .retrieve()
                .bodyToMono(BearerTokenResponse.class)
                .block();
        
        if (response == null) {
            throw new GeneralSecurityException("Null BearerTokenResponse");
        }
        
        return response.access_token;
    }
    
    /**
     * Returns a new JWTBearerToken valid for the next 3 minutes.
     * <p/>
     * The token is signed using TalentBeyondBoundaries private key associated
     * with the certificate associated with the tbbtalent "Connected App"
     * in TBB's Salesforce.
     * <p/>
     * This can be used to get a new access token from Salesforce using the
     * following HTTP request to SF:
     * POST https://login.salesforce.com/services/oauth2/token
     * ?grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=[bearer token]
     * 
     * Content-Type: application/x-www-form-urlencoded"
     * <p/>
     * If all goes well it will return a Bearer access token in a response like
     * this:
     * {
     *     "access_token": "00D1N000002EPj7!AR0AQIaT.sdLMZlsU0Jlm7EPrrWghps9K025kno0nGwg6nf3KmQ9maLukiy20hvnqI1VUw9CY7GhoLIdgN8QsdYOoAk91azE",
     *     "scope": "custom_permissions web openid api id full",
     *     "instance_url": "https://talentbeyondboundaries.my.salesforce.com",
     *     "id": "https://login.salesforce.com/id/00D1N000002EPj7UAG/0051N000005qGt3QAE",
     *     "token_type": "Bearer"
     * }
     * <p/>
     * This code is based on 
     * https://help.salesforce.com/articleView?id=remoteaccess_oauth_jwt_flow.htm&type=5
     * 
     * @return JWTBearerToken
     * @throws GeneralSecurityException If there are signing problems
     */
    private String makeJwtBearerToken() throws GeneralSecurityException {
        String header = "{\"alg\":\"RS256\"}";
        String claimTemplate = "'{'" +
                "\"iss\": \"{0}\", " +
                "\"sub\": \"{1}\", " +
                "\"aud\": \"{2}\", " +
                "\"exp\": \"{3}\"" +
                "'}'";

        StringBuilder token = new StringBuilder();

        //Encode the JWT Header and add it to our string to sign
        token.append(Encoders.BASE64URL.encode(header.getBytes(StandardCharsets.UTF_8)));

        //Separate with a period
        token.append(".");

        //Create the JWT Claims Object
        String[] claimArray = new String[4];
        claimArray[0] = "3MVG9mclR62wycM3f9iy572tIEVmeyMN8eFW5h2BK7eD96hD19zYvpx1vup07kfpCidboRyF56WF3QjL7LAYl";
        claimArray[1] = "jcameron@talentbeyondboundaries.org";
        claimArray[2] = "https://login.salesforce.com";
        claimArray[3] = Long.toString( ( System.currentTimeMillis()/1000 ) + 180);
        MessageFormat claims;
        claims = new MessageFormat(claimTemplate);
        String payload = claims.format(claimArray);

        //Add the encoded claims object
        token.append(Encoders.BASE64URL.encode(payload.getBytes(StandardCharsets.UTF_8)));

        //Sign the JWT Header + "." + JWT Claims Object
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(token.toString().getBytes(StandardCharsets.UTF_8));
        String signedPayload = Encoders.BASE64URL.encode(signature.sign());

        //Separate with a period
        token.append(".");

        //Add the encoded signature
        token.append(signedPayload);

        return token.toString();
    }

    private PrivateKey privateKeyFromPkcs8(String privateKeyPem)
            throws GeneralSecurityException {

        // strip the headers and new lines
        privateKeyPem = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "");
        privateKeyPem = privateKeyPem.replace("-----END PRIVATE KEY-----", "");
        privateKeyPem = privateKeyPem.replaceAll(System.lineSeparator(), "");

        byte[] encoded = Decoders.BASE64.decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static class BearerTokenResponse {
        public String access_token;
        public String scope;
        public String instance_url;
        public String id;
        public String token_type;
    }

    public static class ContactQueryResult {
        public int totalSize;
        public boolean done;
        public List<Contact> records;
        
    }

    public static class CreateRecordResult {
        public String id;
        public boolean success;
        public List<String> errors;
    }

    @Getter
    @Setter
    @ToString
    public class ContactRequest {
        public String AccountId;
        public String FirstName;
        public String LastName;
        public String MailingCountry;
        public String Id;
        public Long TBBid__c;

        public ContactRequest(Candidate candidate) {
            final String country = candidate.getCountry().getName();
            this.MailingCountry = country;

            //Set account id based on candidate's country 
            switch (country) {
                case "Jordan":
                    AccountId = tbbJordanAccountId;
                    break;
                case "Lebanon":
                    AccountId = tbbLebanonAccountId;
                    break;
            }

            User user = candidate.getUser();
            this.FirstName = user.getFirstName();
            this.LastName = user.getLastName();

            this.TBBid__c = Long.valueOf(candidate.getCandidateNumber());
        }
    }
    
}
