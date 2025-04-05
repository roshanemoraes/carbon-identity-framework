package org.wso2.carbon.identity.framework.async.status.mgt.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.wso2.carbon.identity.framework.async.status.mgt.AsyncStatusMgtService;
import org.wso2.carbon.identity.framework.async.status.mgt.AsyncStatusMgtServiceImpl;
import org.wso2.carbon.identity.framework.async.status.mgt.dao.AsyncStatusMgtDAOImpl;

import java.util.logging.Logger;

/**
 * OSGi service component for asynchronous operation status management bundle.
 */
@Component(
        name = "org.wso2.carbon.identity.framework.internal."
                + "AsyncStatusMgtServiceComponent",
        immediate = true
)
public class AsyncStatusMgtServiceComponent {

    private static final Log LOG = LogFactory.getLog(AsyncStatusMgtServiceComponent.class);

    @Activate
    protected void activate(ComponentContext context) {

        BundleContext bundleCtx = context.getBundleContext();
        bundleCtx.registerService(AsyncStatusMgtService.class.getName(), AsyncStatusMgtServiceImpl.getInstance(), null);
        LOG.debug("Async status mgt bundle is activated");
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        BundleContext bundleCtx = context.getBundleContext();
        bundleCtx.ungetService(bundleCtx.getServiceReference(AsyncStatusMgtService.class));
        LOG.debug("Async status mgt bundle is deactivated");
    }
}
