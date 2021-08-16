package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a POST request to validate book being returned by a valid reader is made, an empty set of errors is replied")
    request {
        method 'POST'
        url '/readers/1/commands/validatebookreturning'
        body(
            booksToValidate: [
                [
                    id: $(anyNumber()),
                    readerId: 1],
                [
                    id: $(anyNumber()),
                    readerId: 1]],
            borrowedBooks: []
        )
        headers {
            contentType(applicationJson())
            // required due to https://github.com/spring-cloud/spring-cloud-contract/issues/1428
            //contentType(applicationJsonUtf8())
        }
    }
    response {
        status OK()
        body([])
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