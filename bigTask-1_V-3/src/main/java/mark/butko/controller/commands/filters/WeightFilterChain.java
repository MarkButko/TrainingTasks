package mark.butko.controller.commands.filters;

import static mark.butko.constants.Attributes.WEIGHT_FROM;
import static mark.butko.constants.Attributes.WEIGHT_TO;

import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import mark.butko.controller.util.ControllerUtil;
import mark.butko.model.ApplianceFeatures;
import mark.butko.model.entities.consumers.ElectricalAppliance;

/**
 * Filters given data set by weight range passed as request parameter or session
 * attribute
 * 
 * @see ElectricalAppliance
 * @see FilterChain
 * @author markg
 *
 */
public class WeightFilterChain extends FilterChain {

	@Override
	public Set<ElectricalAppliance> getFiltered(Set<ElectricalAppliance> appliances, HttpServletRequest request) {

		String fromString = request.getParameter(WEIGHT_FROM);
		String toString = request.getParameter(WEIGHT_TO);

		Integer fromAttribute;
		Integer toAttribute;

		if (fromString == null) {
			fromAttribute = (Integer) request.getSession().getAttribute(WEIGHT_FROM);
		} else {
			fromAttribute = ControllerUtil.parseOrZero(fromString);
		}

		if (toString == null) {
			toAttribute = (Integer) request.getSession().getAttribute(WEIGHT_TO);
		} else {
			toAttribute = ControllerUtil.parseOrMax(toString);
		}

		Integer from = ControllerUtil.zeroIfNotValid(fromAttribute);
		Integer to = ControllerUtil.maxIfNotValid(toAttribute, ApplianceFeatures.WEIGHT);

		request.getSession().setAttribute(WEIGHT_FROM, from);
		request.getSession().setAttribute(WEIGHT_TO, to);

		Set<ElectricalAppliance> filteredAppliances = appliances.stream()
				.filter(device -> device.getWeight() <= to && device.getWeight() >= from).collect(Collectors.toSet());

		if (nextChain != null) {
			return nextChain.getFiltered(filteredAppliances, request);
		} else {
			return filteredAppliances;
		}
	}
}
