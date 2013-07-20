Directory services are widely used to store information about employees.
Generally it means that we can use this service to retrieve information about contacts, for example, for co-workers.

This software allows to create bridge between directory service and phone.
I.e., it allows synchronize address book in phone with information about contacts from directory service.

It consists of two main parts.

![Design][image:design]

The first part is a web application that allows access to directory service through REST.
The second part is a native application for phone, that uses web application to access information about contacts.

[image:design]: https://github.com/grytsenko/contacts/blob/master/docs/design.png?raw=true
