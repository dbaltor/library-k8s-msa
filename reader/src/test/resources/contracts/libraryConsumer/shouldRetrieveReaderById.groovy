package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a GET request to retrieve one reader specifying their id is made, the reader is returned")
    request {
        method 'GET'
        url '/readers?reader=1'
    }
    response {
        status OK()
        body([
                id: 1,
                firstName: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                lastName: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                dob: $(anyNumber()),
                address: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                phone: $(producer(regex('[-@./#&+()\\w\\s]+')))
        ])
        headers {
            contentType(applicationJson())
        }
    }
}