package org.ethelred.mc.pack;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import org.ethelred.util.function.CheckedSupplier;
import org.immutables.value.Value;

@Value.Immutable
public interface MCImage extends MCPackFile {
  CheckedSupplier<BufferedImage, IOException> image();

  @Value.Default
  default String formatName() {
    return "png";
  }

  @Override
  default void writeTo(OutputStream out) throws IOException {
    ImageIO.write(image().get(), formatName(), out);
  }

  @Override
  default boolean isBinary() {
    return true;
  }
}
