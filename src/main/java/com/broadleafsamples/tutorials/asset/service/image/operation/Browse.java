/**
 *
 */
package com.broadleafsamples.tutorials.asset.service.image.operation;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.broadleafcommerce.asset.service.image.NamedOperation;

import java.util.HashMap;
import java.util.Map;

/**
 * Operation to convert an image to a category or search/browse-appropriate size.
 *
 * @author Nathan Moore (nathandmoore)
 */
@Component
public class Browse implements NamedOperation {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }

    @Override
    public String getName() {
        return "browse";
    }

    @Override
    public Map<String, String> getOperations() {
        Map<String, String> ops = new HashMap<>(2);
        ops.put("resize", "432x432>");
        // basing this value on one provided here
        // https://www.imagemagick.org/Usage/resize/#resize_unsharp
        ops.put("unsharp", "0x0.75+0.75+0.008");

        return ops;
    }
}
