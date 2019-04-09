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
	tools {
		maven('maven-3.6');
		jdk('java-12-oracle-amd64');
	}
	stages {
		stage ('Build') {
			steps {
				sh('mvn --batch-mode -V -U -e clean install site -Dsurefire.useFile=false');
				archiveArtifacts('target/*.war, target/site/**');
				recordIssues(aggregatingResults: true,
					     enabledForFailure: true,
					     tools: [checkStyle(pattern: '**/checkstyle-result.xml',
							        reportEncoding: 'UTF-8'),
						     pmdParser(pattern: '**/pmd.xml', reportEncoding: 'UTF-8'),
						     cpd(pattern: '**/cpd.xml', reportEncoding: 'UTF-8'),
						     spotBugs(pattern: '**/spotbugsXml.xml', reportEncoding: 'UTF-8',
							      useRankAsPriority: true)
					     ]);
			}
		}
	}
}
