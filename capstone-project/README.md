# Capstone-Project

**Smart Shop** - "**Capstone Project 2**" for "Android Developer Nanodegree Program", developed as per specs developed and approved in ["**Capstone Project 1**"](https://github.com/maliameer/Capstone-Project/blob/master/Capstone_Stage1.pdf)

**NOTE:** This app uses **2 APIs**:
   1. http://api.walmartlabs.com/v1/stores?apiKey=YOUR_API_KEY&zip=06604&format=json       
        Register at https://developer.walmartlabs.com to get your API Key.
      
      then update it at:
      https://github.com/maliameer/Capstone-Project/blob/master/Stage2/smart-shop/gradle.properties
      
      Before runnig the app.
   2. Google Cloud Engine - Endpoint API, developed for this app:
   
        https://smart-shop-206414.appspot.com/_ah/api/products/v1/search/2505   (POST URI)
        
        Above end-point underneath calls Walmart Mobile API to search products like:  
        
        http://search.mobile.walmart.com/search?query=bread&store=2505     
        
        Why above API is not used directly is explained in ["Capstone Project 1"](https://github.com/maliameer/Capstone-Project/blob/master/Capstone_Stage1.pdf)
     
* [Smart Shop Demo](https://www.youtube.com/watch?v=bEEt9SGUrv0)

* **Settings** screen which allows to set Default "**Limit**" and "**Sort Order**" for each Item in a Shop List.
![settings](https://user-images.githubusercontent.com/15310615/41610598-470e637c-73b3-11e8-8749-04d7451b32d8.png)

* **Stores** screen which allows to Search, Add & Remove Stores as Favorites. 
![stores](https://user-images.githubusercontent.com/15310615/41610583-40c9e7ca-73b3-11e8-81ce-01041988e4ba.png)

* **Shop List** screen which allows to Add/Update a Shop List, add multiple Items to it and set "**Limit**" & "**Sort Order**" settings for each one of them.

![shop list](https://user-images.githubusercontent.com/15310615/41610593-45077050-73b3-11e8-8599-7184f4f6733a.png)

* **Shop Lists** screen which allows to List all currently added Shop Lists in the app.
![shop lists](https://user-images.githubusercontent.com/15310615/41610585-42fafe30-73b3-11e8-82e6-92496d70be71.png)

* **Products Search by each Item** screen which gets opened by clicking "**Play**" icon from the Shop List screen against a particular Shop List which then searches Products against each item in the Shop List.

![products search](https://user-images.githubusercontent.com/15310615/41610603-48e0c0f0-73b3-11e8-815d-952089c2ba73.png)

* REST API as exposed via Google Cloud Engine - Endpoint API
![rest api invocation via postman](https://user-images.githubusercontent.com/15310615/41613679-e0997e98-73bb-11e8-9aa8-c350b97fe9e7.png)

**URI**:

https://smart-shop-206414.appspot.com/_ah/api/products/v1/search/2505

**Sample POST JSON Payload**:


      {
       "itemInfoList": [
        {
         "itemName": "bread",
         "limit": 2,
         "sortBy": "lowest_price"
        },
        {
         "itemName": "toilet paper",
         "limit": 3,
         "sortBy": "highest_ratings"
        }
       ]
      }
