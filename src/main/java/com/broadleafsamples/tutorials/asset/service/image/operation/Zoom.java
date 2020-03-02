/**
 *
 */
package com.broadleafsamples.tutorials.asset.service.image.operation;

import org.springframework.stereotype.Component;

import com.broadleafcommerce.asset.service.image.NamedOperation;

import java.util.HashMap;
import java.util.Map;

/**
 * Operation to convert an image to a zoomed-in version. This has lower precedence than other
 * operations like {@link ProductLG}, {@link ProductMD}, etc. so that it can be combined with them.
 *
 * @author Nathan Moore (nathandmoore)
 */
@Component
public class Zoom implements NamedOperation {

    @Override
    public String getName() {
        return "zoom";
    }

    @Override
    public Map<String, String> getOperations() {
        Map<String, String> ops = new HashMap<>(2);
        // basing this value on one provided here
        // https://www.imagemagick.org/Usage/resize/#resize_unsharp
        ops.put("scale", "200%");
        ops.put("unsharp", "0x0.75+0.75+0.008");

        return ops;
    }
}
