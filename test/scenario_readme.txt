A scenario is defined as a <scenario> element with one attribute, the duration of the scenario.

<scenario time="1000">
</scenario>

A scenario contains the following elements:
- (optional) some domains with id's
- (optional) some data with id's
- (optional) some gaussian time distributions with id's
- (optional) some gaussian space distributions with id's
- the map
- the timeline of events

<domain id="dom1" a="0" b="1" c="0"/>
<data id="data1" size="1" domain="dom1"/>
<location id="center"



VALUE
- single, fixed
	- number
- single, random
	- min, max
	- mean, dev
- multiple, fixed
	- list of values
	- count, min, max
	- count, min, max, interval="0"
	- count, first, last
	- count, first, max
	- count, min, last
	- count, interval, first
	- count, interval, last
	- count, interval, min
	- count, interval, max
	- interval, first, max
	- interval, min, last
- multiple, random
	- list of values, dev
	- list of values, count
	- add dev to all multiple, fixed
		- if interval exists, the interval will vary according to the deviation dev
		- if interval does not exist, values themselves will vary with deviation dev


<event type="inject" domain.a="0.5" domain.b.mean="0.7" domain.b.dev="0.3" domain.renew-for.time="true" domain.c="0.9">
	<time first="20" interval="10" count="10" dev="2"/>
	<location y="bottom">
		<x>
			<renew-for domain="true"/>
			<list count="5">
				<item value="10"/>
				<item value="20"/>
				<item value="30"/>
			</list>
		</x>
	</location>
	<for-each time="true" 
</event>



event
	- type
		* type="inject"
	- time=VALUE
	- location
		- x, y = VALUE
		- list of values
		
		* <event.location x="2" y="7"/>
		* location.x="3" location.y="4"
		* location.x.mean="7" location.x.dev="1"
		* location="NE", "SV", "NV", "SE"
		* location.x="E" location.y.
	- data -size
	 	-domain
			-a
			-b
			-c




<scenario time="1000">
	<map type="full" width="31" height="31"/>
	<data type="fixed">
		<item id="0" size="1" a="0" b="1" c="0"/>
		<item id="1" size="1" a="1" b="0" c="0"/>
		<item id="2" size="2" a="0" b="0" c="1"/>
		<item id="3" size="1" a="1" b="0" c="1"/>
		<item id="4" size="2" a="1" b="0" c="0"/>
	</data>
	<timeline>
		<event type="inject" agent="(15,15)" data="0" pressure="0.3"
			persistence="0.1" time="0" />
		<event type="inject" agent="(30,0)" data="1" pressure="0.2"
			persistence="0.1" time="0" />
		<event type="inject" agent="(30,30)" data="2" pressure="0.2"
			persistence="0.1" time="0" />
		<event type="inject" agent="(0,30)" data="3" pressure="0.3"
			time="20" />
		<event type="inject" agent="(15,15)" data="4" pressure="0.3"
			time="20" />
	</timeline>
</scenario>
