
function WebLoadingListener(fxDriver)
{
	this.activeUrls = 0;
    this.fxDriver = fxDriver;
    var docLoaderService = Components.classes["@mozilla.org/docloaderservice;1"].getService(Components.interfaces.nsIWebProgress);
    docLoaderService.addProgressListener(this, Components.interfaces.nsIWebProgress.NOTIFY_STATE_DOCUMENT);
}

WebLoadingListener.prototype.QueryInterface = function(aIID)
{
    if (aIID.equals(Components.interfaces.nsIWebProgressListener) || aIID.equals(Components.interfaces.nsISupportsWeakReference) || aIID.equals(Components.interfaces.nsISupports))
        return this;
    throw Components.results.NS_NOINTERFACE;
}

WebLoadingListener.prototype.onLocationChange = function(webProgress, request, location)
{
    return 0;
};

WebLoadingListener.prototype.onProgressChange = function(webProgress, request, curSelfProgress, maxSelfProgress, curTotalProgress, maxTotalProgress)
{
    return 0;
};

WebLoadingListener.prototype.onSecurityChange = function(webProgress, request, state)
{
    return 0;
};

WebLoadingListener.prototype.onStateChange = function(webProgress, request, stateFlags, aStatus)
{
    if (stateFlags & Components.interfaces.nsIWebProgressListener.STATE_START && stateFlags & Components.interfaces.nsIWebProgressListener.STATE_IS_DOCUMENT)
    {
        this.fxDriver.UrlOpening(request);
    }
    if (stateFlags & Components.interfaces.nsIWebProgressListener.STATE_STOP && stateFlags & Components.interfaces.nsIWebProgressListener.STATE_IS_DOCUMENT)
    {
    	this.fxDriver.UrlOpened(request);
     }
};

WebLoadingListener.prototype.onStatusChange = function(webProgress, request, status, message)
{
    return 0;
};