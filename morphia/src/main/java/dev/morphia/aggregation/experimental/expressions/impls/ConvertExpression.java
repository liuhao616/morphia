package dev.morphia.aggregation.experimental.expressions.impls;

import dev.morphia.aggregation.experimental.codecs.ExpressionCodec;
import dev.morphia.mapping.Mapper;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

public class ConvertExpression extends Expression {
    private final Expression input;
    private final ConvertType to;
    private Expression onError;
    private Expression onNull;

    public ConvertExpression(final Expression input, final ConvertType to) {
        super("$convert");
        this.input = input;
        this.to = to;
    }

    @Override
    public void encode(final Mapper mapper, final BsonWriter writer, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeStartDocument(getOperation());
        ExpressionCodec.writeNamedExpression(mapper, writer, "input", input, encoderContext);
        ExpressionCodec.writeNamedValue(mapper, writer, "to", to.getName(), encoderContext);
        ExpressionCodec.writeNamedExpression(mapper, writer, "onError", onError, encoderContext);
        ExpressionCodec.writeNamedExpression(mapper, writer, "onNull", onNull, encoderContext);
        writer.writeEndDocument();
        writer.writeEndDocument();
    }

    /**
     * The value to return on encountering an error during conversion, including unsupported type conversions.
     *
     * @param onError the value
     * @return this
     */
    public ConvertExpression onError(final Expression onError) {
        this.onError = onError;
        return this;
    }

    /**
     * The value to return if the input is null or missing.
     *
     * @param onNull the value
     * @return this
     */
    public ConvertExpression onNull(final Expression onNull) {
        this.onNull = onNull;
        return this;
    }
}
