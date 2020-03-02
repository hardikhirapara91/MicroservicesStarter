/**
 *
 */
package com.broadleafsamples.tutorials.asset.service.image.operation;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.broadleafcommerce.asset.service.image.NamedOperation;

import java.util.Collections;
import java.util.Map;

/**
 * Operation to convert an image to a thumbnail-appropriate size.
 *
 * @author Nathan Moore (nathandmoore)
 */
@Component
public class Thumbnail implements NamedOperation {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }

    @Override
    public String getName() {
        return "thumbnail";
    }

    @Override
    public Map<String, String> getOperations() {
        return Collections.singletonMap("thumbnail", "96x96>");
    }
}
