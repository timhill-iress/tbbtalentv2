package org.tbbtalent.server.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.Id;

@MappedSuperclass
public abstract class AbstractDomainObject<IdType extends Serializable>  implements Serializable {

    @Id
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @Column(name = "id")
    private IdType id;

    protected AbstractDomainObject() {
    }

    public IdType getId() {
        return id;
    }

    public void setId(IdType id) {
        this.id = id;
    }

    /*
      For good discussion on hashCode and equals for entities see
      https://web.archive.org/web/20170710132916/http://www.onjava.com/pub/a/onjava/2006/09/13/dont-let-hibernate-steal-your-identity.html      
     
      The key problem is that entity objects only get an id once they are
      persisted. If you are using those objects before persisting them
      the absence of an id can lead to peculiar results - for example all
      object instances looking like they are equal.      
     */
    
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || !(obj instanceof AbstractDomainObject)) {
            return false;
        }
        AbstractDomainObject other = (AbstractDomainObject) obj;
        
        //If id is missing assume that it is not equal to other instance.
        //(Previous version of this code treated all instances with null
        //ids as equal).
        if (id == null) return false;

        //Equivalence by id         
        return id.equals(other.id);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[id=" + id + "]";
    }
}
