/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 *
 */
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.awt.geom.Dimension2D;
import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileBreak;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Genealogy;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.WeldingPoint;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamond;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorAndStyle;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileFactoryDelegatorRepeat extends FtileFactoryDelegator {

	public FtileFactoryDelegatorRepeat(FtileFactory factory) {
		super(factory);
	}

	@Override
	public Ftile repeat(Swimlane swimlane, Swimlane swimlaneOut, Display startLabel, final Ftile repeat, Display test,
			Display yes, Display out, HtmlColor color, LinkRendering backRepeatLinkRendering, Ftile backward) {

		final ConditionStyle conditionStyle = skinParam().getConditionStyle();

		final HtmlColor borderColor = getRose().getHtmlColor(skinParam(), ColorParam.activityBorder);
		final HtmlColor backColor = color == null ? getRose().getHtmlColor(skinParam(), ColorParam.activityBackground)
				: color;
		final Rainbow arrowColor = HtmlColorAndStyle.build(skinParam());

		final LinkRendering endRepeatLinkRendering = repeat.getOutLinkRendering();
		final Rainbow endRepeatLinkColor = endRepeatLinkRendering == null ? null : endRepeatLinkRendering.getRainbow();

		final FontConfiguration fcDiamond = new FontConfiguration(skinParam(), FontParam.ACTIVITY_DIAMOND, null);
		final FontConfiguration fcArrow = new FontConfiguration(skinParam(), FontParam.ARROW, null);

		final Ftile backStart = Display.isNull(startLabel) ? null : this.activity(startLabel, swimlane, BoxStyle.PLAIN,
				Colors.empty());

		Ftile result = FtileRepeat.create(backRepeatLinkRendering, swimlane, swimlaneOut, backStart, repeat, test, yes,
				out, borderColor, backColor, arrowColor, endRepeatLinkColor, conditionStyle, this.skinParam(),
				fcDiamond, fcArrow, backward);

		final List<WeldingPoint> weldingPoints = repeat.getWeldingPoints();
		if (weldingPoints.size() > 0) {
			// printAllChild(repeat);

			final Ftile diamondBreak = new FtileDiamond(repeat.skinParam(), backColor, borderColor, swimlane);
			result = assembly(FtileUtils.addHorizontalMargin(result, 10, 0), diamondBreak);
			final Genealogy genealogy = new Genealogy(result);

			final FtileBreak ftileBreak = (FtileBreak) weldingPoints.get(0);

			result = FtileUtils.addConnection(result, new Connection() {
				public void drawU(UGraphic ug) {
					final UTranslate tr1 = genealogy.getTranslate(ftileBreak, ug.getStringBounder());
					final UTranslate tr2 = genealogy.getTranslate(diamondBreak, ug.getStringBounder());
					final Dimension2D dimDiamond = diamondBreak.calculateDimension(ug.getStringBounder());

					final Snake snake = new Snake(arrowColor, Arrows.asToRight());
					snake.addPoint(tr1.getDx(), tr1.getDy());
					snake.addPoint(0, tr1.getDy());
					snake.addPoint(0, tr2.getDy() + dimDiamond.getHeight() / 2);
					snake.addPoint(tr2.getDx(), tr2.getDy() + dimDiamond.getHeight() / 2);
					ug.draw(snake);
				}

				public Ftile getFtile1() {
					return ftileBreak;
				}

				public Ftile getFtile2() {
					return diamondBreak;
				}

			});

		}
		return result;
	}
}
