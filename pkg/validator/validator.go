package validator

import (
	"github.com/go-playground/validator/v10"
)

var validate *validator.Validate

func Init() {
	validate = validator.New()

	_ = validate.RegisterValidation("latitude", func(fl validator.FieldLevel) bool {
		v, ok := fl.Field().Interface().(float64)
		if !ok {
			return false
		}
		return v >= -90 && v <= 90
	})

	_ = validate.RegisterValidation("longitude", func(fl validator.FieldLevel) bool {
		v, ok := fl.Field().Interface().(float64)
		if !ok {
			return false
		}
		return v >= -180 && v <= 180
	})
}

func Struct(s interface{}) error {
	return validate.Struct(s)
}

func Var(field interface{}, tag string) error {
	return validate.Var(field, tag)
}

func FormatValidationErrors(err error) []string {
	if err == nil {
		return nil
	}

	validationErrors, ok := err.(validator.ValidationErrors)
	if !ok {
		return []string{err.Error()}
	}

	messages := make([]string, 0, len(validationErrors))
	for _, e := range validationErrors {
		messages = append(messages, formatFieldError(e))
	}
	return messages
}

func formatFieldError(e validator.FieldError) string {
	field := e.Field()
	switch e.Tag() {
	case "required":
		return field + " is required"
	case "email":
		return field + " must be a valid email"
	case "min":
		return field + " must be at least " + e.Param() + " characters"
	case "max":
		return field + " must be at most " + e.Param() + " characters"
	default:
		return field + " failed validation: " + e.Tag()
	}
}
