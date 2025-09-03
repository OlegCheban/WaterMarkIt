package com.markit.api.builders;

import com.markit.api.Font;

import java.awt.*;

/**
* Text-based watermarks builder
*
* @author Oleg Cheban
* @since 1.3.3
*/
public interface TextBasedWatermarkBuilder<WatermarkBuilderType> {

  /**
   * The color of the text
   *
   * @param color The color for the text
   * @see Color
   */
  TextBasedWatermarkBuilder<WatermarkBuilderType> color(Color color);

  /**
   * The font of the text
   *
   * @param font The font for the text
   * @see com.markit.api.Font
   */
  TextBasedWatermarkBuilder<WatermarkBuilderType> font(Font font);

  /**
   * Makes the text bold
   */
  TextBasedWatermarkBuilder<WatermarkBuilderType> bold();

  /**
   * Adds a trademark symbol to the text
   */
  TextBasedWatermarkBuilder<WatermarkBuilderType> addTrademark();

  /**
   * Finish working with TextBasedWatermarkBuilder and back to the WatermarkBuilderType
   */
  WatermarkBuilderType end();
}