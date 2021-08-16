package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a GET request to retrieve books specifies a non-existent reader id, no borrowed books are returned")
    request {
        method 'GET'
        url '/books?reader=9999'
    }
    response {
        status OK()
        body([[]])
        bodyMatchers {
            jsonPath('$', byType {
				// results in verification of size of array (max 0)
				maxOccurrence(0)
			})
        }
        headers {
            contentType(applicationJson())
        }
    }
}