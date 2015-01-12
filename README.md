eMotoApp Logic Backend:

eMotoUtility Class:
    
    public static eMotoLoginResponse performLogin (String username, String password); //Login in with server: 

eMotoLoginResponse Class: 

    //Properties
    public boolean success;
    public String token;
    public String idle;
    public String username

eMotoAdsCollection Class: 

    //properties 
    public eMotoCell eMotoCell;
    public String token;
    public Map<String,eMotoAds> map =  new HashMap<String,eMotoAds>();

    //methods
    public eMotoAds getAdsWithId (String id);
    public boolean removeAdsWithId (String id);
    public void getAdsCollection (); //fill in Hashmap with eMotoAds object from server, eMotoCell and token must be set before calling this function
    public boolean approveAdsID(String adsID);
    public boolean unapproveAdsWithID(String adsID);


eMotoAds Class:
   
    //methods
    public String id();
    public String scheduleAssetId();
    public String isApprovedStr();
    public String scheduleAssetId()
    public String getAdsImageURL();
    public String getAdsThumbnailURL();

Last modified 12:27pm 12/01/15


Change Logs:
