package main

import (
	"context"
	"fmt"
	"github.com/ngyewch/twirp-playground/rpc"
	"github.com/ngyewch/twirp-playground/rpc2"
	"log"
	"net/http"
	"strings"
)

//go:generate protoc --proto_path=../src/main/proto --go_out=. --go_opt=module=github.com/ngyewch/twirp-playground --twirp_out=. --twirp_opt=module=github.com/ngyewch/twirp-playground service.proto
//go:generate protoc --proto_path=../src/main/proto --go_out=. --go_opt=module=github.com/ngyewch/twirp-playground --twirp_out=. --twirp_opt=module=github.com/ngyewch/twirp-playground model.proto

func main() {
	testServiceHandler := rpc.NewTestServiceServer(&TestServiceServer{})
	testService2Handler := rpc.NewTestService2Server(&TestService2Server{})
	mux := http.NewServeMux()
	mux.Handle(testServiceHandler.PathPrefix(), testServiceHandler)
	mux.Handle(testService2Handler.PathPrefix(), testService2Handler)
	err := http.ListenAndServe(":18080", mux)
	if err != nil {
		log.Fatal(err)
	}
}

type TestServiceServer struct{}

func (s *TestServiceServer) Add(ctx context.Context, request *rpc.AddRequest) (*rpc.AddResponse, error) {
	return &rpc.AddResponse{
		Value: request.GetA() + request.GetB(),
	}, nil
}

func (s *TestServiceServer) DoSomething(ctx context.Context, request *rpc.DoSomethingRequest) (*rpc.DoSomethingResponse, error) {
	if request.GetThrowException() {
		return nil, fmt.Errorf(request.GetExceptionMessage())
	}
	return &rpc.DoSomethingResponse{}, nil
}

type TestService2Server struct{}

func (s *TestService2Server) ToUpper(ctx context.Context, request *rpc2.ToUpperRequest) (*rpc2.ToUpperResponse, error) {
	return &rpc2.ToUpperResponse{
		Text: strings.ToUpper(request.GetText()),
	}, nil
}
