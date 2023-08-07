#!/usr/bin/env groovy

@Library("jenkins-shared-library")

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

        stage("build image") {

            steps {
                script {
                    buildImage 'diaa96/shopme'
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