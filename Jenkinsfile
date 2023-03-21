pipeline {
    agent {
          label 'beanstalk-server'
    }
  
    tools {
      maven 'maven-3.6.3'
    }

    environment {
        // gets short commit id from checked out repo/branch
        GIT_COMMIT = """${sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()}"""
        
        // https://stackoverflow.com/a/62219834/1469797
        GIT_BRANCH = "${GIT_BRANCH.split('/').size() > 1 ? GIT_BRANCH.split('/')[1..-1].join('/') : GIT_BRANCH}"
        
        BucketName="elasticbeanstalk-ap-south-1-887417737466"
        Filename = "${env.GIT_COMMIT}-${params.ENVIRONMENTS}"    
    }

    stages {       
        stage('Setting Variables') {
            steps {
                 script {
             
                     // sh("sed -i 's/-Dnewrelic.config.app_name=abc/-Dnewrelic.config.app_name=${params.ENVIRONMENTS}/g' ${env.WORKSPACE}/.ebextensions/newrelic.config")
                     
                     // Passing Workspace, Region, Environment Type, Environment name 
                     // sh("bash ${env.WORKSPACE}/modifyconf.sh ${env.WORKSPACE}/.ebextensions ${params.AWS_REGION} ${params.ENV_TYPE} ${params.ENVIRONMENTS} ")  
                }
            }
        }       
        
        stage('Code Build') {
            steps {
                script {
                     sh("./gradlew build" )
                     sh ("mv ${env.WORKSPACE}/target/*.zip ${env.WORKSPACE}/target/${env.Filename}.zip ")
                     sh ("cd ${env.WORKSPACE}/target/; zip -r ${env.Filename}.zip *.jar")   
                }
            }
        }
         
        stage('Copy file to S3') {
            steps {
                script {
                     sh ("aws s3 cp ${env.WORKSPACE}/target/*.zip s3://${env.BucketName} --region ${env.AWS_REGION}")
                }
            }
        }
     
    
        stage('Deploy Beanstalk Application') {
            when {
                expression { params.ENVIRONMENTS != '' }
            }
            steps {
                echo "EB Environment names specified, will try to deploy to: ${params.ENVIRONMENTS}"
                script {
                  def environmentNames = "${params.ENVIRONMENTS}".split(',')
                  def appname = "${params.APP_NAME}"
                  def index = 0
                  for(envname in environmentNames) {
                      if(index == 0) {
                           // sh("eb init ${appname} --platform ${env.PLATFORM} --region ${env.AWS_REGION}")
                           // sh("eb use ${envname} ")
                           // sh("eb deploy -l ${env.GIT_COMMIT}-${env.BUILD_TAG}")
                           // sh("eb deploy -l ${env.VERSION}")
                           
                           sh("aws elasticbeanstalk create-application-version --application-name ${appname} --version-label ${env.VERSION} --source-bundle S3Bucket=${env.BucketName},S3Key=${env.Filename}.zip --region ${env.AWS_REGION} ")
                           sh("aws elasticbeanstalk update-environment --environment-name ${envname} --version-label ${env.VERSION} --region ${env.AWS_REGION}")
                      } else {
                          // on subsequent environments we can re-use the application version label with aws cli
                          // to speed up deploys (and we can assume first environment deployed successfully)
                          echo "Not a Environment"
                      }
                      index++
                  }
                }
             }
          }
         
        stage('Health Check') {
            steps {
                 sleep(time:60,unit:"SECONDS")
                 script {
                     sh ("aws elasticbeanstalk describe-environment-health --environment-name ${env.ENVIRONMENTS} --region ${env.AWS_REGION} --attribute-names All")
                 }
            }
        }
         
        stage('Cleanup') {
            steps {
                script {
                    cleanWs() 
                }
            }
        }             
    }
}
