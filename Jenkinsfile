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

      
        stage("Increment version"){
            steps{
                script{
                    echo "Increment app version..."
                    ssh """ 
                    mvn -f ./ShopmeWebParent/ShopmeBackend/ build-helper:parse-version versions:set \
                        -DnewVersion=\${parsedVersion.majorVersion}.\
                        \${parsedVersion.minorVersion}.\
                        \${parsedVersion.nextIncrementalVersion} \
                        versions:commit
                    """

                    ssh """ 
                    mvn -f ./ShopmeWebParent/ShopmeFrontend/ build-helper:parse-version versions:set \
                        -DnewVersion=\${parsedVersion.majorVersion}.\
                        \${parsedVersion.minorVersion}.\
                        \${parsedVersion.nextIncrementalVersion} \
                        versions:commit
                    """
                    
                    def pomContent

                    pomContent = readFile('./ShopmeWebParent/ShopmeBackend/pom.xml')
                    def backVersion = (pomContent =~ '<version>(.+?)</version>')[0][1]

                    pomContent = readFile('./ShopmeWebParent/ShopmeFrontend/pom.xml')
                    def frontVersion = (pomContent =~ '<version>(.+?)</version>')[0][1]

                    env.BACK_IMAGE = "$backVersion-$BUILD_NUMBER"
                    env.FRONT_IMAGE = "$frontVersion-$BUILD_NUMBER"
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
                    buildImage "diaa96/shopme:backend-${BACK_IMAGE}", './ShopmeWebParent/ShopmeBackend/'
                    buildImage "diaa96/shopme:frontend-${FRONT_IMAGE}", './ShopmeWebParent/ShopmeFrontend/'
                    dockerLogin()
                    dockerPush "diaa96/shopme:backend-${BACK_IMAGE}"
                    dockerPush "diaa96/shopme:frontend-${FRONT_IMAGE}"
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