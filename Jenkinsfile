#!/usr/bin/env groovy

library identifier: 'jenkins-shared-library@master', retriever: modernSCM(
        [$class       : 'GitSCMSource',
         remote       : 'https://github.com/DiaaRamadan/jenkins-shared-library.git',
         credentialsId: 'github-creds'
        ]
)

def gv
pipeline {

    agent any

    tools {
        maven 'maven-3.9'
    }

    stages {

       stage("init") {
            steps {
                script {

                    gv = load "script.groovy"
                }
            }
       }

     stage("Increment version") {
        steps {
            script {
                echo "Increment app version..."

                // Run the Maven command to increment the version and commit the change
                def mvnCommand = "mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit"
                sh mvnCommand

                // Read the updated version from the POM file
                def pomContent = readFile('pom.xml')
                def matcher = (pomContent =~ '<version>(.+)</version>')
                def version = matcher[0][1]

                // Set the IMAGE_NAME environment variable with the updated version
                env.IMAGE_NAME = version

                // Echo the updated version for verification
                echo "Updated version: \${env.IMAGE_NAME}"
            }
        }
    }


        stage("build jar") {

            steps {
                script {
                    buildJar()
                }
            }

        }

        stage("build and push image") {

            steps {
                script {
                    buildImage "diaa96/shopme:backend-${IMAGE_NAME}", './ShopmeWebParent/ShopmeBackend/'
                    buildImage "diaa96/shopme:frontend-${IMAGE_NAME}", './ShopmeWebParent/ShopmeFrontend/'
                    dockerLogin()
                    dockerPush "diaa96/shopme:backend-${IMAGE_NAME}"
                    dockerPush "diaa96/shopme:frontend-${IMAGE_NAME}"
                }
            }

        }

        stage("deploy") {

            steps {
                script {
                    gv.deployApp()
                }
            }
        }
    }
}