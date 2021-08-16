package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a POST request to validate book returning by a invalid reader is made, a set of errors is returned")
    request {
        method 'POST'
        url 'readers/9999/commands/validatebookreturning'
        body(
            booksToValidate: [
                [
                    id: $(anyNumber()),
                    readerId: $(anyNumber())],
                [
                    id: $(anyNumber()),
                    readerId: $(anyNumber())]],
            borrowedBooks: []
        )
        headers {
            //contentType(applicationJson())
            // required due to https://github.com/spring-cloud/spring-cloud-contract/issues/1428
            contentType(applicationJsonUtf8())
        }
    }
    response {
        status OK()
        body([])
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