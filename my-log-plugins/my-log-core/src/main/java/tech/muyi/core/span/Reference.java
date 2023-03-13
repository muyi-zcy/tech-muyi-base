package tech.muyi.core.span;

import tech.muyi.core.context.MySpanContext;

import java.util.Objects;

/**
 * @author: muyi
 * @date: 2022/12/30
 **/
public class Reference {
    private final MySpanContext context;
    private final String referenceType;

    public Reference(MySpanContext context, String referenceType) {
        this.context = context;
        this.referenceType = referenceType;
    }

    public MySpanContext getContext() {
        return context;
    }

    public String getReferenceType() {
        return referenceType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reference reference = (Reference)o;
        return Objects.equals(context, reference.context) &&
                Objects.equals(referenceType, reference.referenceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, referenceType);
    }
}
