package config

type EnvConfig struct {
	Port int `envconfig:"PORT" default:"8080" required:"true"`
}

type Accessor interface {
	GetPort() int
}

var _ Accessor = (*EnvConfig)(nil)

func (e *EnvConfig) GetPort() int {
	return e.Port
}
