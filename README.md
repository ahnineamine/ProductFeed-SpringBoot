## Product feed Service
This is a stand-alone service providing the following functionalities:

1. On startup consumes an existing JSON feed from an external API and stores this feed in
   memory.
2. Exposes a REST endpoint to retrieve the information for a single product from the feed
3. Exposes a REST endpoint to retrieve all products of a given category, optionally filtered
   only by those in stock.
4. Exposes a REST endpoint to allow for updating of any of the fields of a single product
5. Exposes a REST endpoint to set the current stock level for a given product

---

## Configuration options
### Environment variables for the external API
The service consumes an External API that provides a Product Feed.
For the purposes of this assessment the feed is provided through a Docker image from DockerHub:  
`https://hub.docker.com/repository/docker/garryturk/mock-product-data`  
which can be ran using the following command:  
`docker run --rm --name mpd -p 4001:4001 garryturk/mock-product-data`  
When ran it will expose a REST endpoint on port 4001.  

One can configure however the source of the product data feed using the following configuration in the `application.properties` file:   
- `external.api.protocol`default "http"   
- `external.api.host`default "localhost"   
- `external.api.port`default "4001"   
- `external.api.endpoint`default "/productdata"
