# WGU-C195
 
This was a school project for building an app for scheduling customer appointments. Here are my compiled notes given to the evaluator:

##  Preface

Data access is a bit slow and thus it takes a second or two to do pretty much anything at all. I wish I had just assumed it to be a single-person application with a single person accessing the database and implemented some caching, but alas, I did not. It is not in the requirements so I shall leave it be.

In fact, there are quite a lot of things I wish I had done differently, but here it is and I shall complain about it no more.

Below you will find notes about each requirement for which I felt needed explanation.

## Log-in Form

Username: test
Password: test

The alternative language is Russian and I tested it with
    Locale.setDefault(new Locale("ru", "RU"));

Note that it was not a requirement, but the language can also be selected from
a dropdown. It will still, however, attempt to detect the system language
and will default to English when no resource file is found.

## Customer Records

The database ERD implies that a single address can have more than one customer.
However, this is not specified in the requirements. For simplicity's sake, I've
treated the customer and address as a one-to-one relationship.

## Calendar Views

I did not implement hover or active styles for the individual entries. A single
click will load the appointment in the details editor.

Work hours were assumed to be 8:00AM-5:00PM system time. Note that a lot of the
test data is already set for a time outside of those hours.

## Exception Controls

It is my personal belief that exceptions should not be used to validate user
input. However, the requirements specify it so I have made it so. In most
input validation situations, I throw an InvalidDataException and then catch it
in the calling method. This should satisfy the "2 different mechanisms"
requirement, at least according to the FAQ.

## Lambda Expressions

Lambdas were used judiciously throughout and I did not provide an inline comment
for most of them. However, you can find the inline comment for two of them in
the updateFilter (around line 180) method of the FXMLCustomersController class.

## Reports

It is not clear to what a consultant refers in the requirements. I have assumed
that it refers to the users. Erring on the side of caution, I implemented two
reports: appointments for each user and for each customer. This should also
satisfy the "one additional report of your choice" requirement.

## Activity Log

The log file can be found in the user's default folder as found by
System.getProperty("user.home"). The file is named "C195Logins.log".
