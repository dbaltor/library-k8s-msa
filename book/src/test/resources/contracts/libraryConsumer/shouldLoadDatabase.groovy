package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a POST request to load books is made, the list of created books is returned")
    request {
        method 'POST'
        url '/books/commands/load'
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