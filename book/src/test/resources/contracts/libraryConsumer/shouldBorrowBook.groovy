package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a POST request to borrow books is made, the books are borrowed")
    request {
        method 'POST'
        url '/books/commands/borrow'
        body([
                readerId: 1,
                bookIds: [$(anyNumber()), $(anyNumber()), $(anyNumber())]
        ])
        headers {
            //contentType(applicationJson())
            // required due to https://github.com/spring-cloud/spring-cloud-contract/issues/1428
            contentType(applicationJsonUtf8())
        }
    }
    response {
        status OK()
        body([[
                id: $(anyNumber()),
                name: $(producer(regex('[-@.,/#&+\'\\w\\s]+'))),
                author: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                genre: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                publisher: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                readerId: 1
        ]])
        bodyMatchers {
            jsonPath('$', byType {
                // results in verification of size of array (exactly 3)
                minOccurrence(3)
                maxOccurrence(3)
            })
        }
        headers {
            contentType(applicationJson())
        }
    }
}