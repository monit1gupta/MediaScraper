package com.mgup.mediascraper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mgup.mediascraper.scraper.service.ScraperService;

@RestController
@RequestMapping("/api/v1/scraper")
public class ScraperController {
	
	@Autowired
	private ScraperService scraperService;

	@GetMapping("/website")
	public ResponseEntity<String> scrapeWebsite(){
		boolean scrapeGoogleNews = scraperService.scrapeGoogleNews();
		return new ResponseEntity<String>(scrapeGoogleNews? "success" : "failure", HttpStatus.OK);
	}
}
