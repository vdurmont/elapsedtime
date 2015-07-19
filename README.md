# ElapsedTime

**ElapsedTime** is a lightweight Java library which generates a string representation of an elapsed time. (e.g.: "2 hours ago").

## Installation

Add the maven dependency to your project or download the library and compile it (`mvn clean install`):

```xml
<dependency>
  <groupId>com.vdurmont</groupId>
  <artificatId>elapsedtime</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage

### Configuration

The configuration is optional and should be done once in your app lifetime.

```java
// Change the default locale (default is ENGLISH)
ElapsedTime.defaultLocale = Locale.FRENCH;
// Change the default smallest division.
// Everything smaller than this will be printed as "Moments ago"
// Default is SECOND
ElapsedTime.smallestTimeDivision = TimeDivision.MINUTE;
```

### Code

```java
// Basic usage
String str1 = ElapsedTime.getFromDurationMillis(500); // Moments ago
String str2 = ElapsedTime.getFromDurationMillis(1000); // 1 second ago
String str3 = ElapsedTime.getFromDurationMillis(3650000); // 1 hour ago
// And so on...

// Specify a locale, just for this call
String str4 = ElapsedTime.getFromDurationMillis(1000, ElapsedTime.Locale.FRENCH); // Il y a 1 seconde

// Alternatives
String str5 = ElapsedTime.getFromDurationSeconds(/** seconds */);
String str6 = ElapsedTime.getFromDurationSeconds(/** seconds */, /** locale */);
String str7 = ElapsedTime.getFromDate(/** a date in the past */);
String str8 = ElapsedTime.getFromDate(/** a date in the past */, /** locale */);
```

### Duration breakdown

| Range | Sample Output
|-|-
| 0 to 750 milliseconds | Moments ago
| 750 milliseconds to 1750 milliseconds | 1 second ago
| 1.75 to 45 seconds | *x* seconds ago
| 45 to 105 seconds | 1 minute ago
| 105 seconds to 45 minutes | *x* minutes ago
| 45 to 105 minutes | 1 hour ago
| 105 minutes to 22 hours | *x* hours ago
| 22 to 36 hours | 1 day ago
| 36 hours to 25 days | *x* days ago
| 26 days to 55 days | 1 month ago
| 56 days to 11 months | *x* months ago
| 11 months to 23 months | 1 year ago
| 23 months and more | *x* years ago

This breakdown was inspired by [momentjs](http://momentjs.com).

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

## License

See [LICENSE.md](/LICENSE.md)
