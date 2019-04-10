#!/usr/bin/groovy
// SPDX-License-Identifier: MIT
// Copyright (c) 2019 Linutronix GmbH
/*
 * CI-RT web application Jenkinsfile
 */
pipeline {
	agent {
		label('master');
	}

	stages {
		stage ('Build with JDK 12') {
			steps {
				withMaven(maven: 'maven-3.6',
					  jdk:'java-12-oracle-amd64',
					  options: [artifactsPublisher(disabled : true)]) {
					sh('mvn -U -e clean install site -Dsurefire.useFile=false');
					recordIssues(aggregatingResults: true,
						     enabledForFailure: true,
						     id: "JDK12",
						     tools: [checkStyle(pattern: '**/checkstyle-result.xml',
									reportEncoding: 'UTF-8'),
							     pmdParser(pattern: '**/pmd.xml',
								       reportEncoding: 'UTF-8'),
							     cpd(pattern: '**/cpd.xml', reportEncoding: 'UTF-8'),
							     spotBugs(pattern: '**/spotbugsXml.xml',
								      reportEncoding: 'UTF-8',
								      useRankAsPriority: true)
						]);
					dir('target-jdk12') {
						deleteDir();
						sh('mv ../target/* .');
					}
					archiveArtifacts('target-jdk12/*.war, target-jdk12/site/**');
				}
			}
		}
	}
}
