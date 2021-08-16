package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a GET request to retrieve books is made without specifying a reader id, all books are returned")
    request {
        method 'GET'
        url '/books?page&size&reader'
    }
    response {
        status OK()
        body([[
                id: $(anyNumber()),
                name: $(producer(regex('[-@.,/#&+\'\\w\\s]+'))),
                author: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                genre: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                publisher: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                readerId: $(anyNumber())
        ]])
        bodyMatchers {
            jsonPath('$', byType {
                // results in verification of size of array (min 1)
                minOccurrence(1)
            })
        }
        headers {
            contentType(applicationJson())
        }
    }
}