package com.phi.cs.view.banner;

/**
 * Actions that implement this interface must implement remainInBanner to check if after a banner refresh can remain into banner.
 * Corresponding entity must implement BannerEntity
 *
 * Created by Alex on 28/07/2015.
 */
public interface BannerAction<T> {
    public boolean remainInBanner(T entity);
}
