package com.mgup.mediascraper.scraper.service.impl;

import java.io.IOException;
import java.util.HashSet;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.mgup.mediascraper.constants.ScraperConstants;
import com.mgup.mediascraper.scraper.service.ScraperService;

@Service
public class ScraperServiceImpl implements ScraperService {

	public boolean scrapeGoogleNews() {

		Document document = new Document(null);

		// TODO: Basic plan is to go to world news home page of google news and get all
		// the images from that and all the links as well,
		// then go to the links and get all possible images, going to next links from
		// those links might be difficult
		// as the links from google news land us to pages of news websites, like CNN,
		// Al-Jazeera, The Times of India, etc.
		// to extract links from then and to make that work will take work as I'll have
		// to analyze all of them and figure out the website structures
		// but images hopefully should be in <img> tags and we can extract images from
		// the individual articles :)

		// Added basic metadata getting logic and image downloading logic, now will work
		// on the actual requirements of the problem.

		try {
			// downloading the target website with an HTTP GET request

			// link for world news in google news
			Connection connection = Jsoup.connect(
					"https://news.google.com/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGx1YlY4U0JXVnVMVWRDR2dKSlRpZ0FQAQ?hl=en-IN&gl=IN&ceid=IN%3Aen");
			document = connection.userAgent(ScraperConstants.USER_AGENT_CONNECTION).get();

			Elements imageElements = document.getElementsByTag("img");
			Elements anchorElements = document.getElementsByTag("a");

			HashSet<String> imageLinkSet = new HashSet<>();
			for (Element element : imageElements) {
				String src = element.attr("src");
				if (src.startsWith("/")) {
					String imageUrl = ScraperConstants.GOOGLE_NEWS_PREFIX + src;
					imageLinkSet.add(imageUrl);
				} else {
					continue;
				}

			}

			System.out.println("images: " + imageLinkSet.size());

//			for (String imageUrl : imageLinkSet) {
//				String downloadedPath = ScraperUtils.downloadImage(imageUrl);
//				System.out.println("image url: " + imageUrl + " downloaded at : " + downloadedPath);
//			}

			HashSet<String> linkSet = new HashSet<>();
			for (Element linkElement : anchorElements) {
				String link = linkElement.attr("href");
				if (link.startsWith("./")) {
					String articleUrl = ScraperConstants.GOOGLE_NEWS_PREFIX + link.substring(1);
					linkSet.add(articleUrl);
				} else {
					continue;
				}
			}

			System.out.println("articles: " + linkSet.size());
			
			for (String link : linkSet) {
				scrapeWebsite(link);
			}

		} catch (IOException ex) {
			System.out.println(ex);
		}

		return true;
	}

	private boolean scrapeWebsite(String websiteLink) {
		Document document = new Document(null);

		try {
			Connection connection = Jsoup.connect(websiteLink);
			document = connection.userAgent(ScraperConstants.USER_AGENT_CONNECTION).get();

			Elements imgElements = document.getElementsByAttribute("img");
			
			HashSet<String> imageLinkSet = new HashSet<>();
			
			for (Element element : imgElements) {
				String imgLink = element.attr("src");
				if (imgLink.startsWith("http")) {
					imageLinkSet.add(imgLink);
				} else {
					System.out.println("Out of format source: " + imgLink);
					continue;
				}
			}
			
			System.out.println(imageLinkSet.size());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

}
