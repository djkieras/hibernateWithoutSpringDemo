# hibernateWithoutSpringDemo
Sample of how to wire standard DataSources and EntityManagers and use annotation-based JPA entities without Spring

I make no warranties and this certainly is not guaranteed to be production-ready code, but it is an example of how 
to use standard DataSources and EntityManagers with Hibernate without Spring and without a JNDI DataSource.  Along 
with some (hopefully) good patterns like using a Factory to create objects to decouple implementation from 
contract, separation of responsibilities, well-defined transactional boundary, and a single point of injection via
Java.  All Hibernate wiring was done in Java without the persistence.xml file.

Why, you may ask, should I want to do this?  Well, perhaps your company does not use Spring (or even CDI).  Perhaps
your teams do not know Spring.  Perhaps you need to make a case that Spring can eliminate a lot of boilerplate
code and potential core Java mistakes.  Whatever the reason, I hope this is useful for you.
