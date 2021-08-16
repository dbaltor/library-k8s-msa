package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a POST request to remove all books is made, the successful message is returned")
    request {
        method 'POST'
        url '/books/commands/cleanup'
    }
    response {
        status OK()
        body('The data have been removed')
    }
}