package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a GET request to retrieve readers without specifying a reader id is made, all readers are returned")
    request {
        method 'GET'
        url '/readers?page&size&reader'
    }
    response {
        status OK()
        body([[
                id: $(anyNumber()),
                firstName: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                lastName: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                dob: $(anyNumber()),
                address: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                phone: $(producer(regex('[-@./#&+()\\w\\s]+')))
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