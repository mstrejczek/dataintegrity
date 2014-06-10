Meta:

Narrative:
As a user
I want to send messages to the system
So that I can achieve a business goal

Scenario: sunny day
Given Application is up and running
When I put a message on the queue
Then value is stored in database
And no message is left on the queue
And outgoing message is sent

Scenario: processing fails after save to DB
Given Application is up and running
When processing is set to fail
And I put a message on the queue
Then value is not stored in database
And message lands on DLQ
And outgoing message is not sent