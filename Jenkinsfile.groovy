pipeline {
    agent {
        label 'ansible'
    }
    // tools {
    //     maven 'Maven'
    //     jdk 'Java11'
    // }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Ayoyinka2456/Jenkins-ansible.git'
            }
        }
        
        stage('Prepping Tomcat and Maven servers') {
            steps {
                sh 'cd Jenkins-ansible/ && ansible-playbook playbook2.yml maven2.yml -i hosts.ini'
            }
        }
        stage('Build') {
            agent {
                label 'Maven'
            }
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            agent {
                label 'Maven'
            }
            steps {
                sh 'mvn test'
                stash(name: 'packaged_code', includes: 'target/*.war')
            }
        }
        stage('Deploy to Tomcat') {
            agent {
                label 'n1_centos n2_ubuntu'
            }
            steps {
                unstash 'packaged_code'
                sh "sudo rm -rf /opt/tomcat/webapp/*.war"
                sh "sudo mv target/*.war /opt/tomcat/webapps/"
                sh "sudo /opt/tomcat/bin/shutdown.sh && sudo /opt/tomcat/bin/startup.sh"
            }
        }
    }}
