#!/bin/bash

/usr/bin/java -Djava.library.path=natives/armhf/osgi \
	-Dlogback.configurationFile=config/logback.xml \
	-cp jlibdpa22x-byteaccess-examples-1.0.0-SNAPSHOT.jar: \
	com.microrisc.dpa22x.byteaccess.examples.Example_LedrPulse
#	com.microrisc.dpa22x.byteaccess.examples.Example_LedrPulse > example.log 2>&1
