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
                    incrementVersion './ShopmeWebParent/ShopmeBackend/'
                    incrementVersion './ShopmeWebParent/ShopmeFrontend/'

                    def backVersion = getPomVersion './ShopmeWebParent/ShopmeBackend/' 
                    def frontVersion = getPomVersion './ShopmeWebParent/ShopmeFrontend/' 

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
                    buildImage "diaa96/shopme:backend-$BACK_IMAGE", './ShopmeWebParent/ShopmeBackend/'
                    buildImage "diaa96/shopme:frontend-$FRONT_IMAGE", './ShopmeWebParent/ShopmeFrontend/'
                    dockerLogin()
                    dockerPush "diaa96/shopme:backend-$BACK_IMAGE"
                    dockerPush "diaa96/shopme:frontend-$FRONT_IMAGE"
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