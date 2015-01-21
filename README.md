# ElapsedTime

**ElapsedTime** is a lightweight Java library which generates a string representation of an elapsed time. (e.g.: "2 hours ago").

## Installation

Download the library and compile it (`mvn clean install`). or just add the maven dependency to your project:

**Soon on maven central.**

```xml
<!-- NOT AVAILABLE ON MAVEN CENTRAL YET -->
<dependency>
  <groupId>com.vdurmont</groupId>
  <artificatId>elapsedtime</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

Optional configuration:
```java
// Change the default locale (default is ENGLISH)
ElapsedTime.defaultLocale = Locale.FRENCH;
// Change the default smallest division.
// Everything smaller than this will be printed as "Moments ago"
// Default is SECOND
ElapsedTime.smallestTimeDivision = TimeDivision.MINUTE;
```

And in your code:
```java
// Basic usage
ElapsedTime.getFromDurationMillis(500); // Moments ago
ElapsedTime.getFromDurationMillis(1000); // 1 second ago
ElapsedTime.getFromDurationMillis(3650000); // 1 hour ago
// And so on...

// Specify a locale, just for this call
ElapsedTime.getFromDurationMillis(1000, ElapsedTime.Locale.FRENCH); // Il y a 1 seconde

// Alternatives
ElapsedTime.getFromDurationSeconds(/** seconds */);
ElapsedTime.getFromDurationSeconds(/** seconds */, /** locale */);
ElapsedTime.getFromDate(/** a date in the past */);
ElapsedTime.getFromDate(/** a date in the past */, /** locale */);
```

## Contribute

### Find a bug or give an idea for a feature

Leave a message in the issues!

### Add a language

*¿Habla usted español? Вы говорите по-русски? 你说中国话？Sprechen Sie Deutsch? Puhutteko suomea?*

Pull requests are more than welcome for new languages!  
Add an entry in the `ElapsedTime.Locale` enum with the right strings and you're done!

### Current todolist

- Accept < 0 duration (e.g.: "in 24 minutes")
- Implement a better personalization of the generated string (e.g.: "1 hour and 32 minutes ago" could be possible)
- Optimize
  - less string instanciations
  - general smaller footprint
  - stop the `divideDuration()` method asap
- Android support? (locale in system)
