package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.parameter.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class DefaultParameterFactorySupplier implements Supplier<List<ParameterFactory>> {

    @Override
    public List<ParameterFactory> get() {
        List<ParameterFactory> rfc5545 = Arrays.asList(new Abbrev.Factory(), new AltRep.Factory(), new Cn.Factory(), new CuType.Factory(),
                new DelegatedFrom.Factory(), new Dir.Factory(), new Encoding.Factory(), new FmtType.Factory(),
                new FbType.Factory(), new Language.Factory(), new Member.Factory(), new PartStat.Factory(),
                new Range.Factory(), new Related.Factory(), new RelType.Factory(), new Role.Factory(),
                new Rsvp.Factory(), new ScheduleAgent.Factory(), new ScheduleStatus.Factory(),
                new SentBy.Factory(), new Type.Factory(), new TzId.Factory(),
                new Value.Factory(), new Vvenue.Factory());

        List<ParameterFactory> rfc7986 = Arrays.asList(new Display.Factory(), new Email.Factory(), new Feature.Factory(),
                new Label.Factory());

        List<ParameterFactory> factories = new ArrayList<>(rfc5545);
        factories.addAll(rfc7986);

        return rfc5545;
    }
}
