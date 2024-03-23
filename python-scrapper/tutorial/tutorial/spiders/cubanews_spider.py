import scrapy
import json
import os
import uuid

class CubanewsSpider(scrapy.Spider):
    name = "cubanews"

    def start_requests(self):
        urls = [
            "https://adncuba.com/noticias-de-cuba",
        ]
        for url in urls:
            yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        # Extract the title of the page
        title = response.css('title::text').extract_first()

        # Extract all links from the page
        links = list(set(response.css('a::attr(href)').extract()))
        
        # Create a directory for output files if it doesn't exist
        output_folder = "scrapping_output"
        if not os.path.exists(output_folder):
            os.makedirs(output_folder)

        # Generate a unique ID for the JSON file
        unique_id = str(uuid.uuid4())

        # Create a JSON object with URL, title, and links
        data = {
            "url": response.url,
            "title": title,
            "links": links
        }

        # Save JSON object to a file
        output_file = f"{output_folder}/{unique_id}.json"
        with open(output_file, "w", encoding="utf-8") as file:
            json.dump(data, file, indent=4)

        self.log(f'Saved URL: {response.url}, Title: {title}, Links: {len(links)} to {output_file}')
