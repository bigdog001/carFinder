package com.cartracker.mobile.android.util.http;
/**
 * Created by jw362j on 7/30/2014.
 */
public interface INetProvider {
    /**
     * add one request into the pool and then execute it.
     * @param req
     */
    void addRequest(INetRequest req);

    /**
     *  add one request into the pool and then execute it based on the priority(INetRequest.PRIORITY_HIGH_PRIORITY: INetRequest.PRIORITY_LOW_PRIORITY:)
     */
    void addRequest(INetRequest req, int priority);

    /**
     * cancle the request in the queue
     */
    void cancel();

    /**
     * stop the network communication
     */
    void stop();
}
