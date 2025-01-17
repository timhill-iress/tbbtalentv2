variable "app" {
  type        = string
  description = "Name of the application"
}

variable "env" {
  type        = string
  description = "Name of the environment"
}

variable "db_public_access" {
  default     = true
  description = "Flag to set if the database publicly accessible"
}

variable "db_enable" {
  default     = false
  description = "Flag to define the app will use an existance database (e.g. test env) or create new one"
}

variable "db_multi_az" {
  type        = bool
  description = "Flag to define if database is multiaz"
}

variable "db_instance_class" {
  type        = string
  description = "The database db instance class"
}

variable "site_domain" {
  type        = string
  description = "The domain name of the website"
}

variable "container_image" {
  type        = string
  description = "The ecr url for the docker image"
}

variable "container_port" {
  type        = number
  description = "Container port"
}

variable "ecs_tasks_count" {
  type        = number
  description = "The desired number of ECS tasks"
  default     = 1
}

variable "aws_access_key" {
  type        = string
  description = "AWS access key to be used by the java application"
}

variable "aws_secret_key" {
  type        = string
  description = "AWS secret key to be used by the java application"
}

variable "s3_bucket" {
  type        = string
  description = "S3 bucket name"
}

variable "es_password" {
  type        = string
  description = "ElasticSearch password"
}

variable "es_url" {
  type        = string
  description = "ElasticSearch URL"
}

variable "es_username" {
  type        = string
  description = "ElasticSearch username"
}

variable "email_default" {
  type        = string
  description = "Default email"
}

variable "email_password" {
  type        = string
  description = "Email password"
}

variable "email_test_override" {
  type        = string
  description = "Test override email"
}

variable "email_user" {
  type        = string
  description = "Email user"
}

variable "drive_id" {
  type        = string
  description = "Google drive candidate drive ID"
}

variable "drive_rootfolder" {
  type        = string
  description = "Google drive root folder ID"
}

variable "drive_list_folders_id" {
  type        = string
  description = "Google drive list folders drive ID"
}

variable "drive_list_folders_root_id" {
  type        = string
  description = "Google drive list folders root ID"
}

variable "drive_private_key" {
  type        = string
  description = "Google drive private key"
}

variable "drive_private_key_id" {
  type        = string
  description = "Google drive private key ID"
}

variable "gradle_home" {
  type        = string
  description = "Gradle home directory"
}

variable "java_home" {
  type        = string
  description = "Java home directory"
}

variable "jwt_secret" {
  type        = string
  description = "JWT secret"
}

variable "m2" {
  type        = string
  description = "M2"
}

variable "m2_home" {
  type        = string
  description = "M2 home directory"
}

variable "sf_privatekey" {
  type        = string
  description = "Salesforce private key"
}

variable "server_port" {
  type        = string
  description = "Server port"
}

variable "server_url" {
  type        = string
  description = "Server URL"
}

variable "slack_token" {
  type        = string
  description = "Slack token"
}

variable "spring_client_url" {
  type        = string
  description = "Spring boot admin client URL"
}

variable "spring_datasource_url" {
  type        = string
  description = "Spring datasource URL"
}

variable "spring_datasource_password" {
  type        = string
  description = "Spring datasource password"
}

variable "spring_datasource_username" {
  type        = string
  description = "Spring datasource username"
}

variable "spring_db_pool_max" {
  type        = string
  description = "Spring database max"
}

variable "spring_db_pool_min" {
  type        = string
  description = "Spring database min"
}

variable "spring_servlet_max_file_size" {
  type        = string
  description = "Spring servlet multipart max file size"
}

variable "spring_servlet_max_request_size" {
  type        = string
  description = "Spring servlet multipart max request size"
}

variable "tbb_cors_urls" {
  type        = string
  description = "TBB Cors URLs"
}

variable "tbb_db_copy_config" {
  type        = string
  description = "TBB partner DB copy config"
}

variable "translation_password" {
  type        = string
  description = "Translation password"
}

variable "web_admin" {
  type        = string
  description = "Web admin URL"
}

variable "web_portal" {
  type        = string
  description = "Candidate portal"
}
