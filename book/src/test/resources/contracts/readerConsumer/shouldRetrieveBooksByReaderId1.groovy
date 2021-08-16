package bookServiceConsumer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("When a GET request to retrieve books specifying a reader id is made, all books borrowed by they are returned")
    request {
        method 'GET'
        url '/books?reader=1'
    }
    response {
        status OK()
        body([[
                id: $(anyNumber()),
                name: $(producer(regex('[-@.,/#&+\'\\w\\s]+'))),
                author: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                genre: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                publisher: $(producer(regex('[-@./#&+\'\\w\\s]+'))),
                readerId: 1
        ]])
        bodyMatchers {
            jsonPath('$', byType {
                // results in verification of size of array (min 1)
                minOccurrence(1)
            })
        }
        /*body([[
                id: $(anyNumber()),
                name: $(anyAlphaNumeric()),
                readerId: 1],
            [
                id: $(anyNumber()),
                name: $(anyAlphaNumeric()),
                readerId: 1],
            [
                id: $(anyNumber()),
                name: $(anyAlphaNumeric()),
                readerId: 1]]
        )
        body '''\
            [{"id" : 0, "name" : "Java", "author" : "", "genre" : "", "publisher" : "", "readerId" : 1},
            {"id" : 0, "name" : "Go", "author" : "", "genre" : "", "publisher" : "", "readerId" : 1},
            {"id" : 0, "name" : "Node", "author" : "", "genre" : "", "publisher" : "", "readerId" : 1}]
        '''*/
        headers {
            contentType(applicationJson())
        }
    }
}