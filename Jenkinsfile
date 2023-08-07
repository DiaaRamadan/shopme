#!/usr/bin/env groovy

library indentifier: 'jenkins-shared-library@master', retriever: modernSCM(
        [
                $class: 'GitSCMSource',
                remote: 'https://github.com/DiaaRamadan/jenkins-shared-library.git',
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
                    buildImage 'diaa96/shopme:backend-1.0', './ShopmeWebParent/ShopmeBackend/'
                    buildImage 'diaa96/shopme:frontend-1.0', './ShopmeWebParent/ShopmeFrontend/'
                    dockerLogin()
                    dockerPush 'diaa96/shopme:backend-1.0'
                    dockerPush 'diaa96/shopme:frontend-1.0'
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