package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a POST request to load readers is made, the list of created readers is returned")
    request {
        method 'POST'
        url '/readers/commands/load?count=1'
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
                // results in verification of size of array (exactly 1)
                minOccurrence(1)
                maxOccurrence(1)
            })
        }
        headers {
            contentType(applicationJson())
        }
    }
}