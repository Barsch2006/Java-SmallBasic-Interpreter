# "SmallBasic" Java

Java program to interpret .sb (Microsoft Small Basic) files.
The original compiles it into executeables and workes is writen in C#.
This program is writen in Java, which makes it running on some systems without the required .NET version.
It is also more customizeable. You can change the syntax if you want to.

## Changes than the original from MS

- Comments are no longer done with singlequoates --> use //
- While-loops end with a `Do`
- Booleans are no longer writen with a capital `True` or a `False` --> use `true` and `false` instead

## ToDos

⚠ you have to add your own lib classes. That will create some more differences to the original ⚠

## Customize

Just add a your compiled `.class` files to the `lib` directory.
**Please mind:** Your classes have to start width _"SB"_ this will be added automaticly by finding the class.
In your `.sb`-file you can then just write the className without the _"SB"_ and without the _"lib."_!

## Running it

```bash
cd src # go into the src folder
javac Main.java
javac ... # compile every java File
java Main "path\to\your\file.sb" # run the program with the path to your .sb file in the args
```

## Docs

### Comments

```php
// This is a comment
```

### Variables

```php
a = "Hallo"
b = 1
c = 0.1
d = false
```

### Built-In-Methods

```java
result = Class.Method(...args)
```

### If-Condition

```php
If true Then
// code
EndIf

If false Then
// code
Else
// code
EndIf
```

### While-Loop

```php
a = 0
While a < 10 Do
a = a + 1
EndWhile
```
