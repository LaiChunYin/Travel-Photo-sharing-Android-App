# Travel-Photo-sharing-Android-App

The app allows users to search, view, and share travel images and blogs with other users. The app has three types of users: [**Guest**](#guest-user), [**Follower**](#follower-user), and [**Followee**](#followee-user).

## Guest User
A guest user can search for public travel images and blogs posted by other users. The search screen provides two options: ***ListView*** and ***MapView***.

•  ListView shows a RecyclerView with images and titles.

•  MapView shows a map with multiple markers pointing to the image locations.


The guest user can filter the images and blogs by category (such as adventure, culture, nature, etc). The filtered results are displayed in the RecyclerView or map. The guest user can tap on a RecyclerView item or a marker to navigate to the next screen, which shows the image and blog details.

The map is centered at a predefined location. The app does not need to retrieve the location for this functionality.

## Follower User
A follower user has the same functionality as a guest user, plus the following features:

•  The follower user can follow their favorite travel bloggers or influencers, and get notified of their latest posts and updates.

•  The follower user can add the posts to their saved posts list.

•  The follower user needs to login or create an account to access these features.

•  The app provides an options menu to navigate to the ***FollowerFolloweeScreen***, which shows a RecyclerView with the followed bloggers or influencers. The follower user can remove any item from the list. The follower user can tap on an item to navigate to the next screen, which shows the profile and posts of the selected blogger or influencer.


## Followee User
A followee user can post their travel images and blogs to share with other users. The app provides an options menu to navigate to the ***PostDetailScreen***, which allows the followee user to provide details for their image and blog. The followee user needs to login or create an account to access this feature.

•  The followee user can view the list of their images and blogs on a RecyclerView. The followee user can tap on an item to navigate to the ***MyPostsScreen*** screen, which shows the image and blog details.

•  The app shows the latitude and longitude of the image location to the database. The app performs geocoding to get the coordinates for a given location and vice-a-versa. The followee user can enter their location or use the ***Use My Current Location*** button, which fetches the device location and performs geocoding on it to obtain the location.

•  The followee user can modify any image and blog details and/or mark the image and blog as public or private. The followee user can also remove any image and blog from the list.


## Data Storage and Authentication
The app saves the data generated by followers and followees using [**Cloud Firestore**](https://www.bing.com/search?form=SKPBOT&q=Cloud%20Firestore) using multiple collections at the root level or using subcollections. The app also uses [**Firebase Authentication Services**](https://www.bing.com/search?form=SKPBOT&q=Firebase%20Authentication%20Services) to provide login, logout and sign-up operations.