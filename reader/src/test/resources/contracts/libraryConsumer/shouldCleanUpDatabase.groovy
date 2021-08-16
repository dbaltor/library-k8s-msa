package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a POST request to remove all readers is made, the successful message is returned")
    request {
        method 'POST'
        url '/readers/commands/cleanup'
    }
    response {
        status OK()
        body('The data have been removed')
    }
}