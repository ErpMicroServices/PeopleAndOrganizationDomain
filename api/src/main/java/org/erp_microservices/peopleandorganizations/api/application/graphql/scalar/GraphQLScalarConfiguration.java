package org.erp_microservices.peopleandorganizations.api.application.graphql.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.UUID;

@Configuration
public class GraphQLScalarConfiguration {

    @Bean
    public GraphQLScalarType uuidScalar() {
        return GraphQLScalarType.newScalar()
                .name("UUID")
                .description("UUID scalar type")
                .coercing(new Coercing<UUID, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof UUID) {
                            return dataFetcherResult.toString();
                        } else if (dataFetcherResult instanceof String) {
                            return dataFetcherResult.toString();
                        }
                        throw new CoercingSerializeException("Expected a UUID object.");
                    }

                    @Override
                    public UUID parseValue(Object input) throws CoercingParseValueException {
                        try {
                            if (input instanceof String) {
                                return UUID.fromString((String) input);
                            }
                        } catch (IllegalArgumentException e) {
                            throw new CoercingParseValueException("Invalid UUID format: " + input);
                        }
                        throw new CoercingParseValueException("Expected a String");
                    }

                    @Override
                    public UUID parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            try {
                                return UUID.fromString(((StringValue) input).getValue());
                            } catch (IllegalArgumentException e) {
                                throw new CoercingParseLiteralException("Invalid UUID format: " + input);
                            }
                        }
                        throw new CoercingParseLiteralException("Expected StringValue");
                    }
                })
                .build();
    }

    @Bean
    public GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar()
                .name("Date")
                .description("Date scalar type")
                .coercing(new Coercing<LocalDate, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDate) {
                            return ((LocalDate) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE);
                        } else if (dataFetcherResult instanceof String) {
                            return dataFetcherResult.toString();
                        }
                        throw new CoercingSerializeException("Expected a LocalDate object.");
                    }

                    @Override
                    public LocalDate parseValue(Object input) throws CoercingParseValueException {
                        try {
                            if (input instanceof String) {
                                return LocalDate.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE);
                            }
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Invalid date format: " + input);
                        }
                        throw new CoercingParseValueException("Expected a String");
                    }

                    @Override
                    public LocalDate parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            try {
                                return LocalDate.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_LOCAL_DATE);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException("Invalid date format: " + input);
                            }
                        }
                        throw new CoercingParseLiteralException("Expected StringValue");
                    }
                })
                .build();
    }

    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("DateTime scalar type")
                .coercing(new Coercing<LocalDateTime, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDateTime) {
                            return ((LocalDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } else if (dataFetcherResult instanceof String) {
                            return dataFetcherResult.toString();
                        }
                        throw new CoercingSerializeException("Expected a LocalDateTime object.");
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
                        try {
                            if (input instanceof String) {
                                return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            }
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Invalid datetime format: " + input);
                        }
                        throw new CoercingParseValueException("Expected a String");
                    }

                    @Override
                    public LocalDateTime parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            try {
                                return LocalDateTime.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException("Invalid datetime format: " + input);
                            }
                        }
                        throw new CoercingParseLiteralException("Expected StringValue");
                    }
                })
                .build();
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(uuidScalar())
                .scalar(dateScalar())
                .scalar(dateTimeScalar());
    }
}
